import java.awt.image.BufferedImage;//управляет изображением, содержимое которого можно записать
import java.awt.Dimension;
import java.awt.Graphics;

class JImageDisplay extends javax.swing.JComponent {//наследование
    public BufferedImage img;

    JImageDisplay(int width, int height, int TYPE_INT_RGB)//конструктор инициализации
    {
        img = new BufferedImage(width, height, TYPE_INT_RGB);//тип определяет,как цвета каждого пикселя будут представлены в изображении
        super.setPreferredSize(new Dimension(width, height));//вызываем метод родительского класса с указанной шириной и высотой.
    //когда будет включен в пользовательский интерфейс, отобразит на экране все изображения
    }

    @Override//переопределение метода JComponent
    protected void paintComponent(Graphics g){//метод для отрисовки, вывод данных изображения
        super.paintComponent(g);//вызываем родительский метод, чтобы объекты отображались правильно
        g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);//нарисовать изображение в компоненте
    }

    public void clearImage(){//устанавливает все пиксели в черный цвет.
        int w = img.getWidth(), h = img.getHeight();
        for (int i = 0; i<h;i++){
            for (int j = 0; j<w; j++){
                img.setRGB(i,j,0);
            }
        }
    }

    public void drawPixel(int x, int y, int rgb){//устанавливает пиксель в определенный цвет
        img.setRGB(x,y,rgb);
    }
}
