# language: es
Característica: HU8.2 Eventos y espacios: horarios y tipo

  Escenario: Ver horarios de un evento
    Dado que abro el detalle de un evento
    Cuando reviso su información
    Entonces veo sus horarios con día, hora de inicio y hora de fin

  Escenario: Horarios por día
    Dado que estoy en la sección de horarios
    Cuando elijo un día de la semana
    Entonces veo las clases y horarios de ese día

  Escenario: Filtrar por tipo
    Dado que estoy en la sección de actividades
    Cuando filtro por tipo (clase, torneo o gimnasio)
    Entonces solo veo los eventos de ese tipo
