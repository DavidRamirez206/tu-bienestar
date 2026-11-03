
# language: es
Característica: HU8.1 Eventos y espacios: consulta

  Escenario: Ver eventos y espacios
    Dado que estoy en la sección de actividades
    Cuando abro la lista
    Entonces veo cada evento con su fecha, lugar y descripción

  Escenario: Ocultar eventos pasados
    Dado que existe un evento que ya terminó
    Cuando consulto los próximos eventos
    Entonces ese evento no aparece en la lista

  Escenario: Filtrar por fecha
    Dado que estoy en la sección de actividades
    Cuando filtro por un rango de fechas
    Entonces solo veo los eventos de ese rango
