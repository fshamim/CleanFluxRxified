package com.fsh.poc.cfr.framework;

import java.io.Serializable;

/**
 * Created by fshamim on 27/11/2016.
 */

public interface IAction extends Serializable {
    Class getAssociatedStore();
}
