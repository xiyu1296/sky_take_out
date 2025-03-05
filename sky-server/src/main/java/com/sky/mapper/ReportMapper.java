package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.vo.SalesTop10ReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReportMapper {

    @Select("select sum(amount) from orders where order_time like concat('%',#{begin},'%') and status=5")
    Double getdayturnover(LocalDate begin);

    Long getUserByTime(LocalDateTime beginTime, LocalDateTime endTime);

    Integer getOrderByTimeAndStatus(Integer status, LocalDateTime begin, LocalDateTime end);

    List<GoodsSalesDTO> getSalesByTime(LocalDateTime begin, LocalDateTime end);
}
