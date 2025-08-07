package org.thingsboard.server.dao.sql.mes.sync;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.thingsboard.server.common.data.mes.sys.TSyncMaterialBom;

import java.util.List;

public interface SyncMaterialBomRepository extends JpaRepository<TSyncMaterialBom,Integer> {

    // 按父级物料id查询
    List<TSyncMaterialBom> findAllByParentId(Integer materialId);

    // 按父级物料id删除
    int deleteByParentId(Integer materialId);

    /**
     * 通过父级物料编码查找
     * @param materialNumber
     * @return
     */
    @Query(value = "select b.* from t_sync_material a " +
            "join t_sync_material_bom b on a.id=b.parent_id " +
            "where a.material_code=?1 ",nativeQuery = true)
    List<TSyncMaterialBom> findByMaterialNumber(String materialNumber);
}
