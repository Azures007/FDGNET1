package org.thingsboard.server.dao.sql.sync;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.TSyncMaterial;
import org.thingsboard.server.common.data.TSyncMaterialBom;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface SyncMaterialBomRepository extends JpaRepository<TSyncMaterialBom,Integer> {

    // 按父级物料id查询
    List<TSyncMaterialBom> findAllByParentId(Integer materialId);

    // 按父级物料id删除
    int deleteByParentId(Integer materialId);

}
