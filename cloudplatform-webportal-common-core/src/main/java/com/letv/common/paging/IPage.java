package com.letv.common.paging;

import java.io.Serializable;

public interface IPage extends Serializable {
    public static final int MAX_RETURN_RECORDS = 1000;

    public static final int RECORDS_PER_PAGE = 15;

    public int getStartRowPosition();

    public int getEndRowPosition();

    public boolean isLastPage();

    public void setTotalRecords(int totalRecords);

    public int getTotalRecords();

    public boolean isTooManySearchReturn();

    public void setTooManySearchReturn(boolean tooManySearchReturn);

    public void setRecordsPerPage(int records);

    public int getRecordsPerPage();

    public void setCurrentPage(int currentPage);

    public int getCurrentPage();

    public int getTotalPages();
    
    public long getPreId();
    
    public void setPreId(long preId);
    
    public long getNextId();
    
    public void setNextId(long nextId);
    
    public int getCurrentRecord();
    
    public void setCurrentRecord(int currentRecord);
}
