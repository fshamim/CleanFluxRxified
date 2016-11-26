package com.fsh.poc.cfr.framework;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by fshamim on 26/11/2016.
 */

public interface IStore {

    Set<String> getActions();

    void processAction(Serializable action);

}
