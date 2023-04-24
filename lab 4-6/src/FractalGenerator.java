import java.awt.geom.Rectangle2D;


/**
 * Этот класс предоставляет общий интерфейс и операции для генерации фракталов,исходный файл
 */
public abstract class FractalGenerator {
    /**
     * Эта статическая вспомогательная функция принимает целочисленную координату и преобразует ее
     * в значение двойной точности, соответствующее определенному диапазону.
     * Он используется для преобразования координат пикселей в
     * значения двойной точности для вычисления фракталов и т.д.
     *
     * @param rangeMin - минимальное значение
     * @param rangeMax - максимальнео значение диапазона
     *
     * @param size размер измерения, из которого берется координата пикселя.
     * Например, это может быть ширина изображения или высота изображения.
     *
     * @param coord - это координата.
     *        Координата должна находиться в диапазоне [0, size].
     */
    public static double getCoord(double rangeMin, double rangeMax,
                                  int size, int coord) {

        assert size > 0;//проверка
        assert coord >= 0 && coord < size;

        double range = rangeMax - rangeMin;
        return rangeMin + (range * (double) coord / (double) size);
    }


    /**
     * метод позволяет генератору фракталов определить наиболее
     * «интересную» область комплексной плоскости
     * для конкретного фрактала.
     */
    public abstract void getInitialRange(Rectangle2D.Double range);


    /**
     * Updates the current range to be centered at the specified coordinates,
     * and to be zoomed in or out by the specified scaling factor.
     */
    public void recenterAndZoomRange(Rectangle2D.Double range,
                                     double centerX, double centerY, double scale) {

        double newWidth = range.width * scale;
        double newHeight = range.height * scale;

        range.x = centerX - newWidth / 2;
        range.y = centerY - newHeight / 2;
        range.width = newWidth;
        range.height = newHeight;
    }


    /**
     * Учитывая координаты <em>x</em> + <em>iy</em> в комплексной плоскости,
     * вычисляет и возвращает количество итераций, прежде чем фрактальная
     * функция покинет ограничивающую область для этой точки.
     */
    public abstract int numIterations(double x, double y);

    public String toString(){
        return this.getClass().getName();
    }//возращает имя способа, по которому будет вычисляться фрактал
}

//Процесс построения фрактала Мандельброта: необходимо
//перебрать все пиксели изображения, рассчитать количество итераций для
//соответствующей координаты, и затем установить пиксель в цвет, основанный
//на количестве рассчитанных итераций