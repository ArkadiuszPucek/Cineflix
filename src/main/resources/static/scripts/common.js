$(document).ready(function () {
    $('.Carousel-module-inner-4oa8u').slick({
        infinite: true,
        speed: 200,
        slidesToShow: 1,
        arrows: true,
        draggable: false,
        centerMode: true,
        variableWidth: true,
    });
});

document.querySelectorAll('.StarButton-module-container-sGC0y').forEach(button => {
    button.addEventListener('click', function () {
        var imdbId = this.getAttribute('data-imdb-id');

        var csrfToken = document.querySelector('meta[name="_csrf"]').content;
        var csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        var headers = new Headers();
        headers.append(csrfHeader, csrfToken);

        var isStarred = this.classList.contains('StarButton-module-isStarred-v2AtG');

        var method = isStarred ? 'DELETE' : 'POST';

        // Utwórz pełny adres URL
        var baseUrl = window.location.origin + '/library/';
        var apiUrl = (isStarred ? 'remove-from-list/' : 'add-to-list/') + imdbId;
        var fullUrl = baseUrl + apiUrl;

        fetch(fullUrl, {
            method: method,
            headers: headers
        })
            .then(response => {
                if (response.ok) {
                    this.classList.toggle('StarButton-module-isStarred-v2AtG', !isStarred);
                }
            })
            .catch(error => console.error('Error:', error));
    });
});


