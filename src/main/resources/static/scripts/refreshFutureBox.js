function refreshFeatureBox() {
    // Wybieramy sekcję do odświeżenia
    const featureBoxContainer = document.getElementById('featureBoxContainer');

    // Tworzymy żądanie HTTP, aby pobrać nową zawartość
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/aktualizacja-zawartosci', true); // Zmodyfikuj ścieżkę URL

    // Obsługa odpowiedzi HTTP
    xhr.onload = function () {
        if (xhr.status === 200) {
            // Aktualizujemy zawartość sekcji
            featureBoxContainer.innerHTML = xhr.responseText;
        }
    };

    // Wysyłamy żądanie
    xhr.send();
}

setInterval(refreshFeatureBox, 5000); // 5000 milisekund = 5 sekund