# language: es
Característica: HU13.2 Interfaz accesible

  Escenario: Contraste suficiente
    Dado que abro cualquier pantalla
    Cuando reviso textos y botones
    Entonces cumplen el contraste mínimo WCAG AA

  Escenario: Navegación por teclado
    Dado que uso solo el teclado
    Cuando recorro una pantalla con Tab y Enter
    Entonces puedo usar todas sus funciones

  Escenario: Textos alternativos
    Dado que una pantalla tiene imágenes o íconos de acción
    Cuando un lector de pantalla los recorre
    Entonces cada uno tiene un texto alternativo
