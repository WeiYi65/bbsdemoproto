package com.coderbbs.bbsdemo.entity;

//封装分页相关信息
public class Page {
    //当前页码
    private int current = 1;
    //显示上限
    private int limit = 10;
    //数据总数，用来计算总页数
    private int rows;
    //查询路径，用来复用页码链接
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {//这里要做一个判断，免得用户输入负数
        if(current>=1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit>=1 && limit<=100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows>=0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //获取当前页的起始行
    public int getOffset(){
        return (current-1)*limit;
    }

    //获取总页数
    public int getTotal(){
        if (rows%limit == 0){//这里是能够整除的意思
            return rows/limit;
        }else{
            return rows/limit +1;
        }
    }

    //获取起始页码
    public int getFrom(){
        int from = current-2;
        return Math.max(from, 1);
    }

    public int getTo(){
        int to = current+2;
        int total = getTotal();
        return Math.min(to, total);
    }

}
