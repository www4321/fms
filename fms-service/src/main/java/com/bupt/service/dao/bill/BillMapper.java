package com.bupt.service.dao.bill;

import com.bupt.service.bean.bill.Bill;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface BillMapper {
    List<Bill> queryPageListByYearMonth(@Param("year")Integer year,@Param("month")Integer month,@Param("startIndex") int startIndex,@Param("pageSize") int pageSize);

    Long queryCountByYearMonth(@Param("year")Integer year,@Param("month")Integer month);

    int batchDelete(List ids);

    int deleteByPrimaryKey(Long id);

    List<Map<String, Object>> getChartsBillYearMonth(@Param("year")Integer year,@Param("month")Integer month);

    int insert(Bill record);

    int insertSelective(Bill record);

    Bill selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Bill record);

    int updateByPrimaryKey(Bill record);

    List<Map<String, Object>> getChartsBillYear(@Param("year")Integer year);
}