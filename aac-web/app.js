'use strict';

// ------------------------------------------------------------------ storage
// Favourites and voice settings live on this device only.
const store = {
  get(key, fallback) {
    try {
      const raw = localStorage.getItem(key);
      return raw === null ? fallback : JSON.parse(raw);
    } catch { return fallback; }
  },
  set(key, value) {
    try { localStorage.setItem(key, JSON.stringify(value)); } catch { /* private mode */ }
  },
};

// ------------------------------------------------------------------ state
let vocabulary = { categories: [] };
let screen = 'home'; // 'home', 'favs', or a category id
let favourites = new Set(store.get('favourites', []));
const settings = { voiceURI: store.get('voiceURI', ''), rate: store.get('rate', 0.9) };

const $ = (id) => document.getElementById(id);
const list = $('list');

// ------------------------------------------------------------------ speech
const synth = window.speechSynthesis;
let voices = [];

function polishVoices() {
  return voices.filter((v) => v.lang.toLowerCase().replace('_', '-').startsWith('pl'));
}

/** Best guess when the user hasn't chosen: prefer higher-quality, on-device Polish voices. */
function defaultVoice() {
  const pl = polishVoices();
  const score = (v) =>
    (/premium/i.test(v.name) ? 4 : 0) +
    (/enhanced|rozszerz|natural|neural/i.test(v.name) ? 2 : 0) +
    (v.localService ? 1 : 0);
  return pl.slice().sort((a, b) => score(b) - score(a))[0] || null;
}

function currentVoice() {
  return voices.find((v) => v.voiceURI === settings.voiceURI) || defaultVoice();
}

function loadVoices() {
  if (!synth) return;
  voices = synth.getVoices();
  fillVoicePicker();
}

function speak(text) {
  $('spoken-text').textContent = text;
  if (!synth) return;
  // A speech failure must never stop the app from working, so guard everything.
  try {
    synth.cancel(); // iOS sometimes gets stuck with a queued utterance
    const u = new SpeechSynthesisUtterance(text);
    u.lang = 'pl-PL';
    const voice = currentVoice();
    if (voice) u.voice = voice;
    u.rate = settings.rate;
    synth.speak(u);
  } catch (e) {
    console.warn('speech failed', e);
  }
}

// ------------------------------------------------------------------ favourites
const favKey = (cat, word) => `${cat.id}|${word.text}`;

function toggleFavourite(cat, word) {
  const key = favKey(cat, word);
  favourites.has(key) ? favourites.delete(key) : favourites.add(key);
  store.set('favourites', [...favourites]);
}

function favouriteWords() {
  return vocabulary.categories.flatMap((cat) =>
    cat.words.filter((w) => favourites.has(favKey(cat, w))).map((w) => [cat, w]));
}

// ------------------------------------------------------------------ navigation
function navigate(target, { push = true } = {}) {
  if (target === screen) return;
  screen = target;
  if (push) {
    // Lets the phone's own Back button return to the home screen.
    if (target === 'home') history.back();
    else if (history.state?.screen && history.state.screen !== 'home') history.replaceState({ screen: target }, '');
    else history.pushState({ screen: target }, '');
  }
  render();
}

window.addEventListener('popstate', (e) => {
  screen = e.state?.screen || 'home';
  render();
});

// ------------------------------------------------------------------ rendering
function el(tag, props = {}, ...children) {
  const node = Object.assign(document.createElement(tag), props);
  node.append(...children);
  return node;
}

function tile(color, emoji, label, onTap, star) {
  const t = el('button', { className: 'tile', ariaLabel: label },
    el('span', { className: 'emoji', ariaHidden: 'true' }, emoji),
    el('span', { className: 'label' }, label));
  t.style.background = color;
  t.addEventListener('click', onTap);
  if (star) t.append(star);
  return t;
}

function starButton(cat, word) {
  const s = el('span', { className: 'star', role: 'button' });
  const refresh = () => {
    const on = favourites.has(favKey(cat, word));
    s.textContent = on ? '★' : '☆';
    s.classList.toggle('on', on);
    s.ariaLabel = on ? 'Usuń z ulubionych' : 'Dodaj do ulubionych';
  };
  refresh();
  s.addEventListener('click', (e) => {
    e.stopPropagation(); // don't also speak the word
    toggleFavourite(cat, word);
    if (screen === 'favs') render(); else refresh();
  });
  return s;
}

function wordTile(cat, word) {
  return tile(cat.color, word.emoji, word.text, () => speak(word.text), starButton(cat, word));
}

function render() {
  list.replaceChildren();
  const cat = vocabulary.categories.find((c) => c.id === screen);

  if (cat) {
    $('title').textContent = `${cat.emoji} ${cat.name}`;
    cat.words.forEach((w) => list.append(wordTile(cat, w)));
  } else if (screen === 'favs') {
    $('title').textContent = '⭐ Ulubione';
    const favs = favouriteWords();
    if (!favs.length) list.append(el('p', { className: 'empty' }, 'Brak ulubionych.\nDotknij ☆ przy słowie, aby je dodać.'));
    favs.forEach(([c, w]) => list.append(wordTile(c, w)));
  } else {
    screen = 'home';
    $('title').textContent = 'Moje Słowa';
    vocabulary.categories.forEach((c) => list.append(tile(c.color, c.emoji, c.name, () => {
      navigate(c.id);
      speak(c.name);
    })));
    const settingsBtn = el('button', { className: 'settings-link' }, '⚙️ Ustawienia głosu');
    settingsBtn.addEventListener('click', openSettings);
    list.append(settingsBtn);
  }

  const atHome = screen === 'home';
  $('back').disabled = atHome;
  $('home').disabled = atHome;
  window.scrollTo(0, 0);
}

// ------------------------------------------------------------------ settings
function fillVoicePicker() {
  const select = $('voice');
  const pl = polishVoices();
  const chosen = currentVoice();
  select.replaceChildren(...pl.map((v) =>
    el('option', { value: v.voiceURI, selected: chosen && v.voiceURI === chosen.voiceURI },
      `${v.name}${v.localService ? '' : ' (internet)'}`)));
  $('voice-hint').textContent = !synth
    ? 'Ta przeglądarka nie obsługuje mowy.'
    : pl.length
      ? 'Lepsze głosy: iPhone – Ustawienia → Dostępność → Treść mówiona → Głosy → Polski. Android – Ustawienia → Zamiana tekstu na mowę.'
      : 'Nie znaleziono polskiego głosu. Pobierz go w ustawieniach telefonu (Treść mówiona / Zamiana tekstu na mowę).';
}

function openSettings() {
  loadVoices();
  $('rate').value = settings.rate;
  $('rate-value').textContent = `${Math.round(settings.rate * 100)}%`;
  $('settings').showModal();
}

$('voice').addEventListener('change', (e) => {
  settings.voiceURI = e.target.value;
  store.set('voiceURI', settings.voiceURI);
});
$('rate').addEventListener('input', (e) => {
  settings.rate = Number(e.target.value);
  $('rate-value').textContent = `${Math.round(settings.rate * 100)}%`;
  store.set('rate', settings.rate);
});
$('test-voice').addEventListener('click', () => speak('Dzień dobry, tak brzmi mój głos.'));

// ------------------------------------------------------------------ wiring
$('back').addEventListener('click', () => navigate('home'));
$('home').addEventListener('click', () => navigate('home'));
$('favs').addEventListener('click', () => { navigate('favs'); speak('Ulubione'); });
$('spoken').addEventListener('click', () => {
  const text = $('spoken-text').textContent;
  if (text && text !== '🔊') speak(text);
});

if (synth) {
  loadVoices();
  synth.addEventListener?.('voiceschanged', loadVoices);
}

if ('serviceWorker' in navigator && location.protocol !== 'file:') {
  navigator.serviceWorker.register('sw.js').catch(() => {});
}

history.replaceState({ screen: 'home' }, '');

fetch('vocabulary.json')
  .then((r) => r.json())
  .then((data) => { vocabulary = data; render(); })
  .catch(() => list.append(el('p', { className: 'empty' }, 'Nie udało się wczytać słów.')));
