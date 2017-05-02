/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.fisheries.uvms.plugins.flux.mapper;

/**
 *
 * @author jojoha
 */
public class MappingException extends Exception {

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable root) {
        super(message, root);
    }

}
