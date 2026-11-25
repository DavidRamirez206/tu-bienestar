
# language: es
Característica: HU7 Subir rutinas prediseñadas

  Escenario: Publicar rutina prediseñada
    Dado que inicié sesión como entrenador
    Cuando creo una rutina prediseñada con nombre, ejercicios y nivel
    Entonces la rutina queda publicada

  Escenario: Consultar rutinas prediseñadas
    Dado que inicié sesión como usuario
    Cuando abro la sección de rutinas prediseñadas
    Entonces veo las rutinas publicadas por los entrenadores

  Escenario: Adoptar rutina prediseñada
    Dado que veo una rutina prediseñada
    Cuando la adopto
    Entonces queda copiada en mis rutinas
    Y puedo editarla sin cambiar la original
