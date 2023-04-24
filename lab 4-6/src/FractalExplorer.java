import java.awt.geom.Rectangle2D;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.SwingWorker;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileFilter;



class FractalExplorer {
    private int disp_size;//размер экрана

    private JImageDisplay img_disp;//ссылка для обновления отображения в разных методах в процессе вычисления фрактала
    private JComboBox<FractalGenerator> combo;//список
    private JButton resetButton, saveButton;//кнопки для сброса изображения и сохранения
    private JFrame frame;//отображение фрактала
    private FractalGenerator fg;//ссылка на базовый класс
    private FractalGenerator[] fractalList = {//список
            new Mandelbrot(),
            new Tricorn(),
            new BurningShip()
    };


    private Rectangle2D.Double rng = new Rectangle2D.Double();//диапазон комплексной плоскости, которая выводится на экран
    private int rowsRemaining;//используем, чтобы узнать, когда будет завершена перерисовка

    FractalExplorer(int screen_size){//конструктор
        this.disp_size = screen_size;//сохраняет значение
        fg = fractalList[0];
        fg.getInitialRange(rng);
    }

    private void createAndShowGUI(){//инициализация графического интерфейса

        frame = new JFrame("Генератор фракталов");

        JPanel topPanel = new JPanel();
        JPanel btmPanel = new JPanel();
        JLabel label = new JLabel("Фрактал:");
        combo = new JComboBox<FractalGenerator>();
        ALClass al = new ALClass();

        resetButton = new JButton("Сброс");//кнопка
        saveButton = new JButton("Сохранить");//кнопка

        for (FractalGenerator fractal: fractalList){
            combo.addItem(fractal);//добавляем реализацию фрактала
        }
        combo.setActionCommand("combo");
        combo.addActionListener(al);
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(al);
        saveButton.setActionCommand("save");
        saveButton.addActionListener(al);

        topPanel.add(label, BorderLayout.CENTER);
        topPanel.add(combo, BorderLayout.CENTER);

        btmPanel.add(resetButton, BorderLayout.CENTER);
        btmPanel.add(saveButton, BorderLayout.CENTER);

        img_disp = new JImageDisplay(disp_size, disp_size,BufferedImage.TYPE_INT_RGB);
        img_disp.addMouseListener(new MLClass());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//закрыть
        frame.setLayout(new java.awt.BorderLayout());//контур кнопки
        //расположения
        frame.add(topPanel,BorderLayout.NORTH);
        frame.add(img_disp, BorderLayout.CENTER);
        frame.add(btmPanel, BorderLayout.SOUTH);

        //размещение содержимого окна,сделают видимым и запретят изменение размеров окна.
        frame.pack();//собирает в одно целое
        frame.setVisible(true);
        frame.setResizable(false);
    }

    //вспомогательный метод для вывода на экран фрактала
    private void drawFractal(){//вывод на экран фрактала
        enableUI(false);//отключает все элементы польз.интерфейса во время рисования
        rowsRemaining = disp_size;//равно общему кол-ву строк, которые нужно нарисовать
        for (int i = 0; i<disp_size; i++){//проходим через каждый пиксель
            new FractalWorker(i).execute();//запустит фоновый поток
        }
    }
    //включает и отключает кнопки с выпадающим списком
    private void enableUI(boolean val){
        combo.setEnabled(val);//включаем или отключаем список
        saveButton.setEnabled(val);//включаем или отключаем кнопку сохранить
        resetButton.setEnabled(val);//включаем или отключаем кнопку сбросить
    }
    //implement, так как это не класс
    private class ALClass implements java.awt.event.ActionListener//реализация интерфейса
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("save")) {
                // настроика средств выбора файлов, чтобы
                //сохранять изображения только в формате PNG,
                JFileChooser chooser = new JFileChooser();
                FileFilter filter = new FileNameExtensionFilter("PNG Images", "png");
                chooser.setFileFilter(filter);
                chooser.setAcceptAllFileFilterUsed(false);//нельзя другие форматы
                if (!(chooser.showSaveDialog(frame)==JFileChooser.APPROVE_OPTION)) return;//можно продолжить операцию по сохранению
                File out = chooser.getSelectedFile();
                try {
                    ImageIO.write(img_disp.img, "png", out);//сохранение
                }
                catch (Exception x){
                    // //информирует пользователя об ошибке
                    JOptionPane.showMessageDialog(frame, x.getMessage(), "Сохранение невозможно", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }
            if (e.getActionCommand().equals("combo")) {
                fg = (FractalGenerator)combo.getSelectedItem();
            }
            fg.getInitialRange(rng);//сброс
            drawFractal();
        }

    }

    private class MLClass implements java.awt.event.MouseListener
    {
        //действия на мышку
        @Override
        public void mouseClicked(MouseEvent e){
            //приложение будет реагировать на щелчки
            //мышью, только в том случае, если больше нет строк, которые должны быть
            //нарисованы.
            if(rowsRemaining>0) return;//то возвращаемся в предыдущее состояние

            double x = FractalGenerator.getCoord(rng.getX(),rng.getX()+rng.getWidth(), disp_size,e.getX());
            double y = FractalGenerator.getCoord(rng.getY(),rng.getY()+rng.getHeight(), disp_size,e.getY());
            fg.recenterAndZoomRange(rng, x, y, 0.5);// отображение пиксельных координат,нажимая на какое-либо место на фрактальном отображении,оно увеличивается
            drawFractal();//перерисовать фрактал
        }

        @Override
        public void mouseExited(MouseEvent e){}
        @Override
        public void mouseEntered(MouseEvent e){}
        @Override
        public void mouseReleased(MouseEvent e){}
        @Override
        public void mousePressed(MouseEvent e){}
    }

    //отвечает за вычисление значений цвета для одной строки фрактала
    private class FractalWorker extends SwingWorker<Object, Object>{//наследование
        private int yCoord;//координата, вычисляемой строки
        private int[] row;//массив чисел для хранения вычисленных значений RGB для каждого пикселя в этой строке

        FractalWorker(int yCoord){//конструктор
            this.yCoord = yCoord;
        }
        //метод в фоновом потоке, а не в потоке событий
        @Override
        protected Object doInBackground(){
            row = new int[disp_size];//выделяем память
            double y = FractalGenerator.getCoord(rng.getY(),rng.getY()+rng.getHeight(), disp_size, yCoord);//получить координату y, соответствующую координате пикселя Y
            //сохраняет каждое значение RGB в соответствующем элементе целочисленного массива.
            for (int j = 0; j<disp_size; j++){
                double x = FractalGenerator.getCoord(rng.getX(),rng.getX()+rng.getWidth(), disp_size, j);//получить координату x, соответствующую координате пикселя X
               //выбор значения цвета на основе кол-ва итераций
                int numIters = fg.numIterations(x,y);//кол-во итераций
                float hue = 0.7f + (float) numIters/200f;
                int rgbColor = Color.HSBtoRGB(hue,1f,1f);
                if (numIters == -1) rgbColor = 0;////если число итераций равно -1, то пиксель устанавливается в черный цвет
                row[j] = rgbColor;
            }
            return null;
        }

        //метод вызывается, когда фоновая задача завершена, вызывается в потоке обработки событий
        @Override
        protected void done(){
            //перебираем массив строк и рисуем
            for (int j = 0; j<disp_size; j++){
                img_disp.drawPixel(j, yCoord, row[j]);
            }
            img_disp.repaint(0,0,yCoord,disp_size,1);//указываем область перерисовки
            rowsRemaining--;
            if (rowsRemaining <= 0){
                enableUI(true);//включаем
            }
        }
    }

    public static void main(String[] args){
        FractalExplorer frex = new FractalExplorer(800);//инициализация с размером
        frex.createAndShowGUI();
        frex.drawFractal();
    }

}
