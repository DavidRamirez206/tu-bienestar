const SECONDS = 3;
const circumference = 283;
const countdownEl = document.getElementById('countdown');
const circle      = document.querySelector('.countdown-circle');
const ring        = document.getElementById('progress-ring');
const cancelBtn   = document.querySelector('a.btn-outline-secondary');
const form        = document.getElementById('logout-form');

ring.style.strokeDasharray  = circumference;
ring.style.strokeDashoffset = 0;

let cancelled = false;
let remaining = SECONDS;

cancelBtn.addEventListener('click', () => { cancelled = true; });

const interval = setInterval(() => {
    if (cancelled) { clearInterval(interval); return; }
    remaining--;
    countdownEl.textContent = remaining;
    ring.style.strokeDashoffset = circumference * (1 - remaining / SECONDS);
    if (remaining === 1) circle.classList.add('ending');
    if (remaining <= 0) { clearInterval(interval); form.submit(); }
}, 1000);