package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Vector;

public class MainFrame extends JFrame {
    private JTable table;
    private JButton open;
    private JPanel panelNorth;
    private File file;
    private JFrame frame = this;


    public MainFrame(){

        table = new JTable();
        open = new JButton("Open File");
        open.addActionListener(new ActionOpen());
        panelNorth = new JPanel();
        panelNorth.add(open);
        frame.add(panelNorth, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }



    private class ActionOpen implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            initFileChooser();
            if(file!=null && file.exists() && file.canRead()){
                frame.revalidate();
                ArrayList<String> arrayList = readFile(file);
                getTable(arrayList);

            }
        }
        private void initFileChooser(){
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = chooser.showOpenDialog(frame);
            if(result == JFileChooser.OPEN_DIALOG){
                file = chooser.getSelectedFile();
            }
        }
        private ArrayList<String> readFile(File file){
            ArrayList<String> arrayList = new ArrayList<>();

            try(FileReader fr = new FileReader(file)) {
                BufferedReader br = new BufferedReader(fr);
                String line;
                int i = 0;
                while ((line=br.readLine()) != null){
                    arrayList.add(i,line);
                    i++;
                }
                br.close();
                fr.close();
                return arrayList;
            } catch (FileNotFoundException e){
                throw new RuntimeException(e + "Невозможно открыть указанный файл");
            }
            catch (IOException e) {
                throw new RuntimeException(e + "Невозможно открыть указанный файл");
            }

        }

        private void getTable(ArrayList<String> arrayList){

            Vector<Vector<String>> value = new Vector<>();
            Vector<String> name = new Vector<>();
            int count =0;
                for (int index = 0; index<arrayList.size(); index++){
                    String i = arrayList.get(index);
                if (i.endsWith(" ")) {
                    i = i.trim();
                    arrayList.set(index, i);
                }
                if (arrayList.indexOf(i) == 0){
                    ArrayList<String> temp = splitArray(i);
                    name.addAll(temp);
                    count = temp.size();
                } else {
                    ArrayList<String> temp = splitArray(i);
                    if (temp.size()==count){
                        value.add(new Vector<>(temp));
                    } else {
                        int r = JOptionPane.showConfirmDialog(frame,
                                "Таблица не может быть отображена, потому что в ней содержится разное количество столбцов",
                                "Ошибка чтения таблицы",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE);
                        if (r == JOptionPane.OK_OPTION){
                            return;
                        }

                    }


                }

            }

            if (frame.getContentPane().getComponentCount()>1){
                frame.getContentPane().remove(frame.getContentPane().getComponent(1));
            }

            Table table = new Table(value, name);
            table.setOpaque(true);
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);
            frame.revalidate();

        }
        private ArrayList<String> splitArray(String line){
            ArrayList<String> words = new ArrayList<>();
            String lineTemp;
            int end;

            while (!line.isEmpty()){
                if (line.startsWith("\"")){
                    if (line.contains("\",")){
                        end = line.indexOf("\",",1) +1;
                        lineTemp = line.substring(1,end);
                        words.add(quotes(lineTemp));
                        line = line.replace(line.substring(0,end+1),"");
                    } else {
                        lineTemp = line.substring(1);
                        words.add(quotes(lineTemp));
                        line = line.replace(line,"");
                    }
                } else if (line.contains(",")){
                    end = line.indexOf(",");
                    lineTemp = line.substring(0,end);
                    if (lineTemp.startsWith(" ") && !line.replace(" ", "").isEmpty()){
                        lineTemp = lineTemp.replace(lineTemp.substring(0,1),"");
                    }
                    words.add(lineTemp);
                    line = line.replace(line.substring(0, end+1),"");

                } else {
                    words.add(line);
                    line = line.replace(line, "");
                }
            }




            return words;
        }
        private String quotes(String line) {
            StringBuilder item = new StringBuilder();
            int end;
            while (!line.isEmpty()) {
                if (line.contains("\"\"")) {
                    end = line.indexOf("\"\"");
                    item = item.append(line, 0, end + 1);
                    line = line.replace(line.substring(0, end + 2), "");
                } else {
                    if (!line.replace("\"", "").isEmpty()) {
                        end = line.indexOf("\"");
                        item = item.append(line, 0, end);
                        line = line.replace(line.substring(0, end + 1), "");
                    } else {
                        line = line.replace("\"", "");
                    }
                }

            }
            return item.toString();
        }

    }
    public class Table extends JPanel{
        public Table(Vector<Vector<String>> value, Vector<String> name){
            super(new GridLayout(1,0));

            table = new JTable(value, name);
            table.setPreferredScrollableViewportSize(new Dimension(500, 70));
            table.setFillsViewportHeight(true);
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane);
        }
    }





}
