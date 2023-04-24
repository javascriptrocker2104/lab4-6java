import java.awt.geom.Rectangle2D;

class Mandelbrot extends FractalGenerator{//наследование

    public static final int MAX_ITERATIONS = 2000;//константа с маx. кол-вом итераций

    /** метод позволяет генератору фракталов определить наиболее
     «интересную» область комплексной плоскости для конкретного фрактала.
     Методу в качестве аргумента передается прямоугольный объект, и метод должен изменить поля
     прямоугольника для отображения правильного начального диапазона для фрактала**/
    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -1.5;
        range.width = range.height = 3;
    }

    /**
     * Вычисляем Zn = Zn-1 ^ 2 + c, Z0 = 0, а c - особая точка в фрактале, который мы показываем (заданный x и y).
     */
    @Override
    public int numIterations(double x, double y) {
        double temp, re = x, im = y;
        int i;
        for (i = 0;(i < MAX_ITERATIONS)&&!(re*re+im*im>4); i++){
            temp = re*re-im*im+x;
            im = 2*re*im+y;
            re = temp;
        }
        if (i>=MAX_ITERATIONS) return -1;//показываем,что точка не выходит за границы

        return i;
    }
}
