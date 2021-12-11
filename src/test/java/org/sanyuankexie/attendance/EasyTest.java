package org.sanyuankexie.attendance;

import com.alibaba.excel.EasyExcel;
//import com.alibaba.excel.read.listener.PageReadListener;
//import com.alibaba.excel.metadata.CellData;
//import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
//import org.sanyuankexie.attendance.TestMapper.UserInsertMapper;
//import org.sanyuankexie.attendance.entry.User;
import org.sanyuankexie.attendance.mapper.UserInsertMapper;
import org.sanyuankexie.attendance.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;


//@SpringBootTest
@Slf4j
public class EasyTest
{
    String path="E:\\work\\";
//    private List<User> data2() {
//        List<User> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            User user=new User().setString(""+i);
////                    .setDept(i+"").setEmail(i+"").setLocation(""+i);
//
//            list.add(user);
//        }
//
//        return list;
//    }
    private List<User> data() {
        List<User> list = new ArrayList<User>();
        for (int i = 0; i < 10; i++) {
            User data = new User();
            data.setId(111L);
            data.setEmail(222+"");
            data.setLocation("1312");
            data.setGithubId("shuf");
            data.setDept("seuf");
            list.add(data);
        }
        return list;
    }
    @Autowired
    UserInsertMapper userMapper;
//    @Test
    public void simpleRead() {
        // 写法1：JDK8+ ,不用额外写一个UserListener
        // since: 3.0.0-beta1
        String fileName = path +  "21.xlsx";

        final Integer[] sum = {0};
       final Integer[] in={0};
       Integer[] up={0};
        EasyExcel.read(fileName, User.class, new PageReadListener<User>(dataList -> {
            for (User demoData : dataList) {
                sum[0]++;

                if (isNull(demoData)){
                    info(demoData,"用户数据不全(除github)");
                    continue;
                }

                if (!checkMail(demoData.getEmail())){
                    info(demoData,"邮箱检验不通过");
                    continue;
                }

                if ( String.valueOf(demoData.getId()).length()!="2000300223".length()){
                    info(demoData,"邮箱长度不正确");
                    continue;

                }
                String[] location={"5109","5102","5108"};
               if(Arrays.stream(location).noneMatch(v-> v.equals(demoData.getLocation()))) {
                   info(demoData,"所在位置存在问题");
                   continue;
               }
                String[] dept={"多媒体部","软件部","硬件部"};
                if(Arrays.stream(dept).noneMatch(v-> v.equals(demoData.getDept()))) {
                    info(demoData,"所在部门存在问题");
                    continue;
                }

                QueryWrapper<User> userQueryWrapper=new QueryWrapper<>();

                userQueryWrapper.eq("id",demoData.getId());
                User user = userMapper.selectOne(userQueryWrapper);

                if (user!=null){
                    List<String> change = getChange(user, demoData);
                    if (change.size()==0){
                        continue;
                    }else{
                        userMapper.updateById(demoData);
                        info(user,"更新了数据("+ change+")");
                        up[0]++;
                    }
                }else{
                    userMapper.insert(demoData);
                    in[0]++;
                }
            }
        })).sheet().doRead();
        System.out.println("总共人数:"+sum[0]+" 修改人数:"+up[0]+" 新增人数:"+in[0]);
    }

    public  void info(User user,String msg){

        System.out.println(user.getId() + "(" + user.getName() + ")" + ":" + msg);
    }
    public  boolean isNull(User user){

        return  user.getId()==null||user.getDept()==null||user.getLocation()==null||user.getName()==null||user.getEmail()==null;

    }

    public boolean checkMail(String email){
        String regEx="^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}$";
        return Pattern.matches(regEx,email);
    }

    public List<String> getChange(User oU,User nU){
        List<String> info=new ArrayList<>();
        Field[] declaredFields = User.class.getDeclaredFields();
        for(Field f:declaredFields){
            f.setAccessible(true);
            try {
                if (!String.valueOf(f.get(oU)).equals(String.valueOf(f.get(nU)))){
                   info.add(f.get(oU)+"->"+f.get(nU));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return info;
    }
}
