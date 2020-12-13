/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.events;

/**
 *
 * @author marlo
 */
public interface AppointmentEventListener {
	void newAppointment(NewAppointmentEvent _event);
	void updatedAppointment(UpdatedAppointmentEvent _event);
	void deletedAppointment(DeletedAppointmentEvent _event);
}
