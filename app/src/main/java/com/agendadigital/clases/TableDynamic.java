package com.agendadigital.clases;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class TableDynamic {
    private TableLayout tableLayout;
    private Context context;
    private String[] header;
    private ArrayList<String[]>data;
    private TableRow tableRow;
    private TextView txtCell;
    private int indexC;

    public TableDynamic(TableLayout tableLayout, Context context) {
        this.tableLayout=tableLayout;
        this.context=context;
    }

    public void addHeader(String[] header){
        this.header=header;
        createHeader();
    }
    public void addData(ArrayList<String[]>data){
        this.data=data;
        createDataTable();
    }


    private void newRow(){
        tableRow = new TableRow(context);
    }

    private void newCell(){
        txtCell = new TextView(context);
        txtCell.setTextColor(Color.BLUE);
        txtCell.setGravity(Gravity.CENTER);
        txtCell.setTextSize(18);
    }
    private void createHeader(){
        indexC=0;
        newRow();
        while (indexC<header.length){
            newCell();
            txtCell.setText(header[indexC++]);
            tableRow.addView(txtCell,newTableRowParams());
        }
        tableLayout.addView(tableRow);
    }
    @SuppressLint("SetTextI18n")
    private void createDataTable(){
        String info;
        int indexR;
        for (indexR = 1; indexR <= data.size(); indexR++ ){
            newRow();
            String[] columns=data.get(indexR - 1);
            for (indexC = 0; indexC<header.length; indexC++){

                info = (indexC < columns.length)?columns[indexC]:"";

                    newCell();
                    txtCell.setText(info);
                    tableRow.addView(txtCell,newTableRowParams());

            }
            tableLayout.addView(tableRow);
        }
    }

    private TableRow.LayoutParams newTableRowParams(){
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setMargins(1,1,1,1);
        params.weight = 1;
        return params;
    }
    public void cargarNotas(TableLayout tableLayout,Context context){
        tableLayout.removeAllViews();
        ArrayList<String[]> rows =new ArrayList<>();
        AdminSQLite datos = new AdminSQLite(context,"dbReader",null,1);
        SQLiteDatabase database = datos.getWritableDatabase();

        String[] campos = new String[] {"descri", "nota1","nota2", "nota3"};

        Cursor c = database.query("notas", campos, null, null, null, null, null);


        final TableDynamic tableDynamic = new TableDynamic(tableLayout,context);
        String[] header = {"MATERIA","TRIM.1","TRIM.2","TRIM.3"};
        tableDynamic.addHeader(header);
        if (c.moveToFirst()){
            while (!c.isAfterLast()){

                String mate = c.getString(0);
                String not1 = c.getString(1);
                String not2 = c.getString(2);
                String not3 = c.getString(3);

                String[]fila= {mate,not1,not2,not3};
                rows.add(fila);
                c.moveToNext();
            }
            tableDynamic.addData(rows);
            database.close();
        }
    }

    public void cargarPagos(TableLayout tableLayout,Context context){
        tableLayout.removeAllViews();
        ArrayList<String[]> rows =new ArrayList<>();
        AdminSQLite datos = new AdminSQLite(context,"dbReader",null,1);
        SQLiteDatabase database = datos.getWritableDatabase();

        String[] campos = new String[] {"detalle", "fecha","recnum", "haber"};

        Cursor c = database.query("kar_alu", campos, null, null, null, null, null);


        final TableDynamic tableDynamic = new TableDynamic(tableLayout,context);
        String[] header = {"DETALLE","FECHA","RECIBO","MONTO"};
        tableDynamic.addHeader(header);
        if (c.moveToFirst()){
            while (!c.isAfterLast()){

                String mate = c.getString(0);
                String not1 = c.getString(1);
                String not2 = c.getString(2);
                String not3 = c.getString(3);

                String[]fila= {mate,not1,not2,not3};
                rows.add(fila);
                c.moveToNext();
            }
            tableDynamic.addData(rows);
            database.close();
        }
    }

}
