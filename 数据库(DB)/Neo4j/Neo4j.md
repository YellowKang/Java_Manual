# 创建数据库

## docker-compose

```bash
cat > ./docker-compose.yaml << EOF
version: '3'
services:
  neo4j:
    image: neo4j:5
    container_name: neo4j
    ports:
      - 7474:7474
      - 7687:7687
    environment:
      - NEO4J_AUTH=neo4j/123456
EOF
```

# 创建索引

```

CREATE INDEX FOR (n:Case) ON (n.caseNo);

CREATE INDEX FOR (p:Person) ON (p.matchId);

CREATE INDEX FOR (v:Vehicle) ON (v.vin);

CREATE INDEX FOR (p:Phone) ON (p.phone);

CREATE INDEX FOR (a:Account) ON (a.accountId);

CREATE INDEX FOR (r:Repair) ON (r.repairName);

CREATE INDEX  FOR (a:Appraisal) ON (a.organization);
```

# SpringBoot整合

maven依赖

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-neo4j</artifactId>
		</dependency>
```

配置文件

```properties
spring:
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: 123456
  data:
    neo4j:
      database: neo4j
```

Service

```java
  private final ReactiveNeo4jClient neo4jClient;

    public Mono<ResponseData<NodeRelationshipVo>> searchCase(SearchCaseDto dto) {

        // 查询案件所有的关系以及节点，limit300条，返回node（节点），relationships（关系），depth（深度）
        String query = "MATCH path = (:Case {caseNo: $caseNo})-[*]-(related)\n" +
                "  RETURN nodes(path) as nodes, relationships(path) as relationships,length(path) as depth LIMIT 300;";

        return neo4jClient.query(query)
                .bind(dto.getCaseNo()).to("caseNo")
                .fetch()
                .all()
                .collectList()
                .flatMap(result -> {
                    // 转换为Node+关系
                    NodeRelationshipVo relationshipVo = NodeRelationshipVo.build(result);
                    return Mono.just(ResponseData.success(relationshipVo));
                });
    }
```

Vo

```java

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRelationship;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HuangKang
 * @date 2024/1/10 09:59:24
 * @describe 节点关系Vo
 */
@Data
public class NodeRelationshipVo {
    private List<NodeVo> nodes = new ArrayList<>();

    private Set<RelationshipVo> relations = new HashSet<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeVo {

        private String elementId;

        private String type;

        private List<String> labels;

        private Map<String, Object> properties;

        private Long depth;

        public String getType() {
            if (labels != null && labels.size() > 0) {
                return labels.get(0);
            }
            return null;
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelationshipVo {

        private String startNodeId;

        private String endNodeId;

        private String type;

    }

    /**
     * 返回nodes 以及 relationships
     * "MATCH path = (:Case {caseNo: $caseNo})-[*]-(related)" +
     * "  RETURN nodes(path) as nodes, relationships(path) as relationships;"
     *
     * @param maps
     * @return
     */
    public static NodeRelationshipVo build(List<Map<String, Object>> maps) {
        NodeRelationshipVo result = new NodeRelationshipVo();
        Map<String, Object> nodeIdMap = new HashMap<>();
        for (Map<String, Object> map : maps) {
            List<InternalNode> nodes = (List<InternalNode>) map.get("nodes");
            List<InternalRelationship> relationships = (List<InternalRelationship>) map.get("relationships");

            Long depth = (Long) map.get("depth");
            List<NodeVo> nodeList = new ArrayList<>();
            for (InternalNode v : nodes) {
                if (nodeIdMap.get(v.elementId()) == null) {
                    nodeIdMap.put(v.elementId(), depth);
                    nodeList.add(new NodeVo(v.elementId(), null, v.labels().stream().toList(), v.asMap(), depth));
                }
            }
            // 转换关系
            Set<RelationshipVo> relationshipVos = relationships.stream().map(v -> {
                return new RelationshipVo(v.startNodeElementId(), v.endNodeElementId(), v.type());
            }).collect(Collectors.toSet());

            result.nodes.addAll(nodeList);
            result.relations.addAll(relationshipVos);
        }
        if( !result.nodes.isEmpty()){
            result.nodes = result.nodes.stream().sorted(Comparator.comparing(NodeVo::getDepth)).toList();
        }
        return result;
    }

}
```

删除解绑关系

```java
@Query("MATCH (a:Account)-[r:骑手]->(c:Case) WHERE id(a) = $accountId AND id(c) = $caseId DELETE r")
void deleteRelationship(String caseNo, String matchId);
```
