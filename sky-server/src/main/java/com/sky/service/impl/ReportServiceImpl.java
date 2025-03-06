package com.sky.service.impl;

import com.aliyun.oss.common.utils.StringUtils;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.spi.LocaleServiceProvider;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private WorkspaceService workspaceService;

    public TurnoverReportVO getturnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> timelist = new ArrayList<>();
        List<Double> turnoverlist = new ArrayList<>();
        timelist.add(begin);
        Double turnover = reportMapper.getdayturnover(begin);
        if(turnover == null)turnover = (double) 0;
        turnoverlist.add(turnover);


        while(!begin.isEqual(end)){
            begin = begin.plusDays(1);
            turnover = reportMapper.getdayturnover(begin);
            if(turnover == null)turnover = (double) 0;

            timelist.add(begin);
            turnoverlist.add(turnover);
        }

        String finaltl = timelist.stream()
                .map(LocalDate::toString)
                .collect(Collectors.joining(","));

        String finaltol = turnoverlist.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setTurnoverList(finaltol);
        turnoverReportVO.setDateList(finaltl);

        return turnoverReportVO;
    }

    public UserReportVO getuserStatistics(LocalDate begin, LocalDate end) {
    List<LocalDate> dateList = new ArrayList<>();
    List<Long> newUserList = new ArrayList<>();
    List<Long> totalUserList = new ArrayList<>();

    dateList.add(begin);
    Long newUser = reportMapper.getUserByTime(begin.atTime(LocalTime.of(0, 0)), end.atTime(LocalTime.of(23, 59, 59)));
    Long totalUser = reportMapper.getUserByTime(null, end.atTime(LocalTime.of(23, 59, 59)));
    newUser = newUser == null ? 0 : newUser;
    totalUser = totalUser == null ? 0 : totalUser;
    newUserList.add(newUser);
    totalUserList.add(totalUser);

    while (!begin.isEqual(end)) {
        begin = begin.plusDays(1); // 更新日期

        newUser = reportMapper.getUserByTime(begin.atTime(LocalTime.of(0, 0)), end.atTime(LocalTime.of(23, 59, 59)));
        totalUser = reportMapper.getUserByTime(null, end.atTime(LocalTime.of(23, 59, 59)));
        newUser = newUser == null ? 0 : newUser;
        totalUser = totalUser == null ? 0 : totalUser;

        newUserList.add(newUser);
        totalUserList.add(totalUser);
        dateList.add(begin);
    }

    // 构建 UserReportVO 对象
    return UserReportVO.builder()
            .dateList(dateList.stream().map(LocalDate::toString).collect(Collectors.joining(","))) // 将日期列表转为逗号分隔的字符串
            .totalUserList(totalUserList.stream().map(String::valueOf).collect(Collectors.joining(","))) // 将总量用户列表转为逗号分隔的字符串
            .newUserList(newUserList.stream().map(String::valueOf).collect(Collectors.joining(","))) // 将新增用户列表转为逗号分隔的字符串
            .build();
}

    public OrderReportVO getorderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> trueOrderList = new ArrayList<>();
        List<Integer> totalOrderList = new ArrayList<>();

        dateList.add(begin);
        Integer trueOrder = reportMapper.getOrderByTimeAndStatus(5,begin.atTime(LocalTime.of(0, 0)),begin.atTime(LocalTime.of(23, 59, 59)));
        Integer totalOrder = reportMapper.getOrderByTimeAndStatus(null,begin.atTime(LocalTime.of(0, 0)),begin.atTime(LocalTime.of(23, 59, 59)));
        trueOrder = trueOrder == null ? 0 : trueOrder;
        totalOrder = totalOrder == null ? 0 : totalOrder;
        trueOrderList.add(trueOrder);
        totalOrderList.add(totalOrder);

        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1); // 更新日期

            trueOrder = reportMapper.getOrderByTimeAndStatus(5,begin.atTime(LocalTime.of(0, 0)),begin.atTime(LocalTime.of(23, 59, 59)));
            totalOrder = reportMapper.getOrderByTimeAndStatus(null,begin.atTime(LocalTime.of(0, 0)),begin.atTime(LocalTime.of(23, 59, 59)));
            trueOrder = trueOrder == null ? 0 : trueOrder;
            totalOrder = totalOrder == null ? 0 : totalOrder;

            trueOrderList.add(trueOrder);
            totalOrderList.add(totalOrder);
            dateList.add(begin);
        }

        // 构建 UserReportVO 对象
        return OrderReportVO.builder()
                .dateList(dateList.stream().map(LocalDate::toString).collect(Collectors.joining(","))) // 将日期列表转为逗号分隔的字符串
                .validOrderCountList(trueOrderList.stream().map(String::valueOf).collect(Collectors.joining(","))) //
                .orderCountList(totalOrderList.stream().map(String::valueOf).collect(Collectors.joining(","))) //
                .totalOrderCount(totalOrderList.stream().mapToInt(Integer::valueOf).sum())
                .validOrderCount(trueOrderList.stream().mapToInt(Integer::valueOf).sum())
                .orderCompletionRate(totalOrderList.stream().mapToDouble(Integer::valueOf).sum()/trueOrderList.stream().mapToDouble(Integer::valueOf).sum())
                .build();
    }

    public SalesTop10ReportVO getsalesStatistics(LocalDate begin, LocalDate end) {

        List<GoodsSalesDTO> salesTop10ReportVOS = reportMapper.getSalesByTime(begin.atTime(LocalTime.of(0,0,0)),end.atTime(LocalTime.of(23,59,59)));
        List<String> nameList = new ArrayList<>();
        List<String> numberList = new ArrayList<>();


        for(GoodsSalesDTO s : salesTop10ReportVOS){
            String name = s.getName()==null? " " :s.getName();
            String number = s.getNumber()==null?" " : String.valueOf(s.getNumber());

            nameList.add(name);
            numberList.add(number);
        }



        return SalesTop10ReportVO.builder()
                .nameList(nameList.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .numberList(numberList.stream().map(String::valueOf).collect(Collectors.joining(",")))
                .build();
    }

    public void exportDate(HttpServletResponse response) throws IOException {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDateTime begintime = begin.atTime(LocalTime.of(0,0));
        LocalDateTime endtime = end.atTime(LocalTime.of(23,59,59));
        List<BusinessDataVO> busslist = new ArrayList<>();

        //查询数据
        BusinessDataVO totalstatistic = workspaceService.getBusinessData(begintime,endtime);
        BusinessDataVO tmp = workspaceService.getBusinessData(begintime,begin.atTime(LocalTime.of(23,59,59)));
        if(tmp == null){
            tmp.setNewUsers(0);
            tmp.setTurnover((double) 0);
            tmp.setUnitPrice((double)0);
            tmp.setOrderCompletionRate((double)0);
            tmp.setValidOrderCount(0);
        }
        busslist.add(tmp);
        LocalDate tmpdate = begin;
        while(!tmpdate.isEqual(end)){
            tmpdate = tmpdate.plusDays(1);
            tmp = workspaceService.getBusinessData(tmpdate.atTime(LocalTime.of(0,0)),tmpdate.atTime(LocalTime.of(23,59,59)));
            if(tmp == null){
                tmp.setNewUsers(0);
                tmp.setTurnover((double) 0);
                tmp.setUnitPrice((double)0);
                tmp.setOrderCompletionRate((double)0);
                tmp.setValidOrderCount(0);
            }
            busslist.add(tmp);
        }

        //插入excel
        InputStream in = null;
        XSSFWorkbook excel = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

            excel = new XSSFWorkbook(in);
            XSSFSheet sheet = excel.getSheet("Sheet1");

            XSSFRow timerow = sheet.getRow(1);
            timerow.getCell(1).setCellValue("时间："+begintime + "至" + endtime);

            XSSFRow totalrow1 = sheet.getRow(3);
            totalrow1.getCell(2).setCellValue(totalstatistic.getTurnover());
            totalrow1.getCell(4).setCellValue(totalstatistic.getOrderCompletionRate());
            totalrow1.getCell(6).setCellValue(totalstatistic.getNewUsers());

            XSSFRow totalrow2 = sheet.getRow(4);
            totalrow2.getCell(2).setCellValue(totalstatistic.getValidOrderCount());
            totalrow2.getCell(4).setCellValue(totalstatistic.getUnitPrice());

            tmpdate = begin;
            for(int i = 0; i < busslist.size() ; i++){
                XSSFRow row = sheet.getRow(7 + i);
                BusinessDataVO businessData = busslist.get(i);
                row.getCell(1).setCellValue(String.valueOf(tmpdate));
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());

                tmpdate = tmpdate.plusDays(1);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            ServletOutputStream servletOutputStream = response.getOutputStream();
            excel.write(servletOutputStream);

            servletOutputStream.close();
            in.close();
            excel.close();
        }

        //输出到浏览器

    }

}
