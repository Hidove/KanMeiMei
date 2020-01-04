package cn.hidove.meimei.mapper;


import cn.hidove.meimei.model.ImageListModel;
import cn.hidove.meimei.model.ImageUrlModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ImageUrlMapper {


    @Select("select * from meimei_image where updatetime=0 limit 1")
    List<ImageUrlModel> imageUrl();

    @Update("${sql}")
    int execute(String sql);

    @Update("update meimei_image set updatetime=#{updatetime} where id = #{id}")
    int updateUpdatetimeById(Integer id,long updatetime);
}
