package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Activity;

import java.time.LocalDate;
import java.util.List;

public interface ActivityService {
    /**
     * Lista los eventos ordenados por fecha.
     *
     * @param upcoming si es true omite los que ya terminaron
     * @param from     si llega, omite los que terminan antes de esta fecha
     * @param to       si llega, omite los que empiezan despues de esta fecha
     */
    List<Activity> search(boolean upcoming, LocalDate from, LocalDate to);
    Activity findById(Integer id);
    Activity save(Activity activity);
    Activity update(Integer id, Activity activity);
    void delete(Integer id);
}
