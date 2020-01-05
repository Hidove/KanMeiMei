package cn.hidove.meimei.mapper;


import cn.hidove.meimei.model.ImageListModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.data.jdbc.repository.query.Query;

import java.util.List;

@Mapper
public interface ImageListMapper {

    @Select("select * from meimei_list where updatetime=0 limit 1")
    List<ImageListModel> imageUrl();

    @Update("update meimei_list set updatetime=#{updatetime} where id = #{id}")
   int update(long id, long updatetime);

    @Update("${sql}")
    void execute(String sql);
}