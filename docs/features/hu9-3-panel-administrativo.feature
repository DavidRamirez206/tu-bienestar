# language: es
Característica: HU9.3 Panel administrativo: ejercicios y eventos

  Escenario: Gestionar ejercicios y eventos
    Dado que inicié sesión como administrador
    Cuando creo, edito o elimino un ejercicio o un evento
    Entonces el cambio se refleja para todos los usuarios

  Escenario: Opciones solo para el administrador
    Dado que inicié sesión con el rol de usuario
    Cuando abro el catálogo de ejercicios o la lista de eventos
    Entonces no veo las opciones de crear, editar ni eliminar

  Escenario: Datos incompletos
    Dado que soy administrador y estoy creando un ejercicio o evento
    Cuando intento guardarlo sin los datos obligatorios
    Entonces el sistema no lo guarda y me indica qué falta
