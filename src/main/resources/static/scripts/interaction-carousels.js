// Pobierz elementy HTML z przyciskami "Poprzedni" i "Następny"
const prevButton = document.querySelector('.Carousel-module-prev-EJuzt');
const nextButton = document.querySelector('.Carousel-module-next-AXbEv');

// Pobierz elementy HTML zawierające listę seriali i przewijaną zawartość
const carouselContainer = document.querySelector('.Carousel-module-carousel-NbPjL');
const carouselInner = carouselContainer.querySelector('.Carousel-module-inner-4oa8u');

// Ustaw początkową pozycję przewijania na 0
let scrollPosition = 0;

// Funkcja do przewijania w lewo
function scrollLeft() {
    scrollPosition -= 100; // Możesz dostosować wartość przewijania według potrzeb
    carouselInner.style.transform = `translateX(${scrollPosition}px)`;
}

// Funkcja do przewijania w prawo
function scrollRight() {
    scrollPosition += 100; // Możesz dostosować wartość przewijania według potrzeb
    carouselInner.style.transform = `translateX(${scrollPosition}px)`;
}

// Dodaj obsługę kliknięcia na przycisk "Poprzedni"
prevButton.addEventListener('click', scrollLeft);

// Dodaj obsługę kliknięcia na przycisk "Następny"
nextButton.addEventListener('click', scrollRight);
