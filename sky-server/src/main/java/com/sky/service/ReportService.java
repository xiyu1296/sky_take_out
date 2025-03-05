package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ReportService {
    TurnoverReportVO getturnoverStatistics(LocalDate begin, LocalDate end);

    UserReportVO getuserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getorderStatistics(LocalDate begin, LocalDate end);

    SalesTop10ReportVO getsalesStatistics(LocalDate begin, LocalDate end);
}
