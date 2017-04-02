package kpi.ipt.labs.distributed.computions.calculator.local;

import java.io.Serializable;

public interface RealFunction extends Serializable {

    double apply(double x);
}