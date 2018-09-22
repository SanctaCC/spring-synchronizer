package com.sanctacc.springsynchronizer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Object on which locking takes place.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class LockedResource {

    private Method method;

    private List<Object> lockedParameters;
}