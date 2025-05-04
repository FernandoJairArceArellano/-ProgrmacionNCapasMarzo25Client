package com.Digis01.FArceProgramacionNCapas.ML;

import java.util.List;

public class Result <T> {

    public boolean correct; // true / false
    public String errorMessage; // mensaje descriptivo
    public Exception ex; // maneja toda la exception
    public T object;
    public List<T> objects;
}
