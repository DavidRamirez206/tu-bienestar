
# language: es
Característica: HU4 Registrar progreso

  Escenario: Registrar progreso
    Dado que tengo una rutina
    Cuando registro repeticiones, tiempo y nivel de esfuerzo de un ejercicio
    Entonces el registro se guarda con la fecha

  Escenario: Datos inválidos
    Dado que estoy registrando mi progreso
    Cuando dejo campos vacíos o ingreso valores negativos
    Entonces el sistema no guarda el registro
    Y me explica qué dato está mal

  Escenario: Editar un registro reciente
    Dado que tengo un registro de progreso reciente
    Cuando corrijo sus valores
    Entonces el registro queda actualizado
