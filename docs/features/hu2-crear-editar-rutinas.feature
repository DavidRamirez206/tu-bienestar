
# language: es

Característica: HU2 Crear y editar rutinas

  Escenario: Crear rutina con ejercicios predefinidos
    Dado que inicié sesión
    Cuando creo una rutina con ejercicios del catálogo
    Entonces la rutina se guarda
    Y aparece en mi lista de rutinas

  Escenario: Agregar ejercicio personalizado
    Dado que el ejercicio que quiero no existe en el catálogo
    Cuando agrego un ejercicio personalizado a mi rutina
    Entonces el ejercicio queda incluido solo en mis rutinas
    Y no aparece en el catálogo de otros usuarios

  Escenario: Editar rutina propia
    Dado que tengo una rutina propia
    Cuando agrego, quito o modifico sus ejercicios
    Entonces los cambios se guardan
