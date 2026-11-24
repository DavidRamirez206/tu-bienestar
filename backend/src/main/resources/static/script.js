
//Manejo de límite de input en el formulario de Registro
document.addEventListener("DOMContentLoaded", () => {
    const ageInputs = document.querySelectorAll(".age-input");

    ageInputs.forEach(input => {
        input.addEventListener("keydown", (event) => {
            if (['-', '+', 'e', 'E'].includes(event.key)) {
                event.preventDefault();
            }
        });

        input.addEventListener("input", () => {
            let value = parseInt(input.value) || 0;

            if (value > 99) input.value = 99;
            if (value < 0) input.value = 0;
        });
    });

    //Manejo de los Toasts
    document.querySelectorAll('.toast').forEach(t => {
        new bootstrap.Toast(t).show();
    });
});