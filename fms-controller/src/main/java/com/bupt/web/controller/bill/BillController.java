package com.bupt.web.controller.bill;

import com.bupt.common.bean.Pager;
import com.bupt.common.bean.PagerResult;
import com.bupt.common.bean.Result;
import com.bupt.service.bean.bill.Bill;
import com.bupt.service.dao.bill.BillMapper;
import com.bupt.web.controller.BaseController;
import com.bupt.web.controller.bill.bean.BillCreateBean;
import com.bupt.web.controller.bill.bean.UpdateReqestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import java.math.BigDecimal;
import java.util.*;


@Controller
@RequestMapping("/bill")
public class BillController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(BillController.class);
    @Autowired
    BillMapper billMapper;
    @ResponseBody
    @RequestMapping("/bill_type")
    public Result BillType(){
       String[] result = {"饮食","服饰美容","生活日用","住房缴费","交通出行","通信物流","运动健康","娱乐","文教","医疗","旅行","投资","人情","其他消费"};
       return success(result);
    }
    @ResponseBody
    @RequestMapping("/create_bill")
    public Result createBill(@RequestBody BillCreateBean billCreateBean){
        logger.info("createBill RequestParam{type:{},date:{},description:{},money:{}}",billCreateBean.getType(),billCreateBean.getDate(),billCreateBean.getDescription(),billCreateBean.getMoney());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(billCreateBean.getDate());
        Bill bill = new Bill();
        bill.setConsumeType(billCreateBean.getType());
        bill.setConsumeInfo(billCreateBean.getDescription());
        bill.setConsumeCreate(billCreateBean.getDate());
        bill.setMoney(billCreateBean.getMoney());
        bill.setYear(calendar.get(Calendar.YEAR));
        bill.setMonth(calendar.get(Calendar.MONTH) + 1);
        billMapper.insert(bill);
        return success(null);
    }
    @ResponseBody
    @RequestMapping("/list_bill")
    public PagerResult getConsumeBillList(@RequestParam(value="year") int year,@RequestParam(value="month") int month,@RequestParam(value="page") int currentPage,@RequestParam(value="pageSize") int pageSize){
        logger.info("getConsumeBillList request params. year:{}, month:{}, currentPage:{}, pageSize{}",year,month,currentPage,pageSize);
        int startIndex = (currentPage - 1) * pageSize;
        Long total = billMapper.queryCountByYearMonth(year, month);
        List<Bill> billList  = billMapper.queryPageListByYearMonth(year,month,startIndex,pageSize);
        logger.info("total:{},billList:{}",total,billList.size());
        Pager pager = new Pager(currentPage,pageSize,total);
        PagerResult result = new PagerResult();
        result.setErrCode(0);
        result.setErrMsg("success");
        result.setPagination(pager);
        result.setList(billList);
        return result;
    }
    @ResponseBody
    @RequestMapping("/remove_bill")
    public Result removeBill(@RequestParam(value="id") Long id){
        logger.info("removeBill request params. id:{}",id);
        int result = billMapper.deleteByPrimaryKey(id);
        if(result != 1){
            return failed("remove bill fail. bill id:"+id);
        }
        return success(null);
    }
    @ResponseBody
    @RequestMapping("/batch_remove_bill")
    public Result batchRemoveBill(@RequestParam(value="ids") String ids){
        logger.info("removeBill request params. id:{}",ids);
        String idList[] = ids.split(",");
        List idLists = new ArrayList();
        for(String id : idList){
            idLists.add(Long.parseLong(id));
        }
        logger.info("idList:{}",idLists);
        int result = billMapper.batchDelete(idLists);
        if(result != idLists.size()){
            return failed("batch remove bill fail. bill ids:"+ids);
        }
        return success(null);
    }
    @ResponseBody
    @RequestMapping("/update_bill")
    public Result updateConsumeBill(@RequestBody UpdateReqestBean updateReqestBean){
        logger.info("updateConsumeBill request params. updateReqestBean:{}",updateReqestBean.toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(updateReqestBean.getDate());
        Bill bill = new Bill();
        bill.setId(updateReqestBean.getId());
        bill.setConsumeType(updateReqestBean.getType());
        bill.setConsumeInfo(updateReqestBean.getName());
        bill.setConsumeCreate(updateReqestBean.getDate());
        bill.setMoney(updateReqestBean.getMoney());
        bill.setYear(calendar.get(Calendar.YEAR));
        bill.setMonth(calendar.get(Calendar.MONTH) + 1);
        int result = billMapper.updateByPrimaryKeySelective(bill);
        if(result != 1){
            return failed("update bill fail. bill id:"+updateReqestBean.getId());
        }
        return success(null);
    }
    @ResponseBody
    @RequestMapping("/charts_bill/year_month")
    public Result getChartsBillYearMonth(@RequestParam(value="year") int year,@RequestParam(value="month") int month){
        logger.info("getChartsBillYearMonth request params. year:{}, month:{}",year,month);
        List<Map<String, Object>> datas = billMapper.getChartsBillYearMonth(year, month);
        logger.info("datas List<Map<String, Object>>:{}", datas.toString());
        String key = null;
        BigDecimal value = null;
        Map<String,BigDecimal> result = new HashMap<>();
        for(Map<String, Object> data : datas){
            for ( Map.Entry<String,Object> entry : data.entrySet()) {
                if("type".equals(entry.getKey())){
                    key = (String) entry.getValue();
                } else if("count".equals(entry.getKey())){
                    value = (BigDecimal) entry.getValue();
                }
            }
            result.put(key,value);
        }
        logger.info("result map:{}", result.toString());
        return success(result);
    }
    @ResponseBody
    @RequestMapping("/charts_bill/year")
    public Result chartsBillYear(@RequestParam(value="year") int year){
        logger.info("chartsBillYear request params. year:{}",year);
        List<Map<String, Object>> datas = billMapper.getChartsBillYear(year);
        Integer key = null;
        BigDecimal value = null;
        Map<Integer,BigDecimal> result = new HashMap<>();
        for(Map<String, Object> data : datas){
            for ( Map.Entry<String,Object> entry : data.entrySet()) {
                if("month".equals(entry.getKey())){
                    key = (Integer) entry.getValue();
                } else if("count".equals(entry.getKey())){
                    value = (BigDecimal) entry.getValue();
                }
            }
            result.put(key,value);
        }
        logger.info("result map:{}", result.toString());
        return success(result);
    }
}
