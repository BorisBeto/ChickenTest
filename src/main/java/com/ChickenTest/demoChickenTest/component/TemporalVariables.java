package com.ChickenTest.demoChickenTest.component;

import com.ChickenTest.demoChickenTest.entity.Chicken;
import com.ChickenTest.demoChickenTest.entity.Egg;

import java.util.ArrayList;
import java.util.List;

/*  Variables temporales que se utilizaran para identificar la cantidad de Huevos y Pollos que posee la granja
*   en función de los días transcurridos.
*   *** Estas variables son utilizadas por el metodo FarmService.pasarDias(int) ***
* */
public class TemporalVariables {
    public static int countEggs = 0;
    public static int countEggsSell = 0;
    public static int countNewEggs = 0;
    public static int countChicken = 0;
    public static int countChickensSell = 0;
    public static int countNewChickens = 0;
    public static List<Egg> listEggsToSell = new ArrayList<>();
    public static List<Chicken> listChickensToSell = new ArrayList<>();

    public static boolean isExccessEggs = false;
}
