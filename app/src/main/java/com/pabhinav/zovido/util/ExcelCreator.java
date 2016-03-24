package com.pabhinav.zovido.util;


import com.pabhinav.zovido.alertdialogs.ZovidoAlertInputDialog;
import com.pabhinav.zovido.application.ZovidoApplication;
import com.pabhinav.zovido.pojo.SavedLogsDataParcel;

import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

/**
 * @author pabhinav
 */
public class ExcelCreator {

    private static final String[] columnHeaders = {
            "Name",
            "Phone Number",
            "Agent Name",
            "Call Date",
            "Call Time",
            "Call Duration",
            "Purpose",
            "Product",
            "Sport",
            "Call Remarks"
    };
    public static final int numberOfColumns = columnHeaders.length;


    /**
     * Create excel labels, these can be used to create worksheet excel.
     *
     * @return worksheet labels.
     * @throws Exception
     */
    public static Label[][] createExcelLabels() throws Exception {

        if(ZovidoApplication.getInstance() == null){
            throw new IllegalStateException("No Application instance available");
        }

        /**
         * Number of columns are same as number of header entries
         * Number of rows are one (header row) plus total number of saved logs **/
        Label[][] labels = new Label[numberOfColumns][ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size() + 1];

        /** Set header Cells **/
        labels = setHeaderLabels(labels);

        /** Set normal cells **/
        labels = setNormalLabels(labels);

        return labels;
    }

    private static Label[][] setHeaderLabels(Label[][] labels) throws Exception{

        for(int i = 0;i<numberOfColumns; i++){
            labels[i][0] = new Label(i, 0, columnHeaders[i]);
            labels[i][0].setCellFormat(getHeaderCellFormat());
        }
        return labels;
    }

    private static Label[][] setNormalLabels(Label[][] labels) throws Exception{

        for(int i = 1; i<=ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().size(); i++){

            SavedLogsDataParcel savedLogsDataParcel = ZovidoApplication.getInstance().getSavedLogsDataParcelArrayListInstance().get(i-1);
            labels[0][i] = new Label(0,i, savedLogsDataParcel.getName());
            labels[1][i] = new Label(1,i, savedLogsDataParcel.getPhoneNumber());
            labels[2][i] = new Label(2,i, savedLogsDataParcel.getAgentName());
            labels[3][i] = new Label(3,i, savedLogsDataParcel.getDate());
            labels[4][i] = new Label(4,i, savedLogsDataParcel.getTime());
            labels[5][i] = new Label(5,i, savedLogsDataParcel.getDuration());
            labels[6][i] = new Label(6,i, savedLogsDataParcel.getPurpose());
            labels[7][i] = new Label(7,i, savedLogsDataParcel.getProduct());
            labels[8][i] = new Label(8,i, savedLogsDataParcel.getSport());
            labels[9][i] = new Label(9,i, savedLogsDataParcel.getCallRemarks());

            for(int j = 0; j<numberOfColumns; j++){
                labels[j][i].setCellFormat(getNormalCellFormat());
            }
        }
        return labels;
    }

    private static WritableCellFormat getNormalCellFormat() throws  Exception{
        WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
        WritableCellFormat cellFormat = new WritableCellFormat(cellFont);

        //center align the cells' contents
        cellFormat.setAlignment(Alignment.CENTRE);

        return cellFormat;
    }

    private static WritableCellFormat getHeaderCellFormat() throws Exception {
        WritableFont headerFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        WritableCellFormat headerFormat = new WritableCellFormat(headerFont);

        //center align the cells' contents
        headerFormat.setAlignment(Alignment.CENTRE);

        return headerFormat;
    }
}
