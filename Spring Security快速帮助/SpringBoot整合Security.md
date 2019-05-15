查询所有没有行业的文档

db.getCollection('accident').find({"hangye":{"$exists":false}})

查询所有没有行业的文档的



,{multi:true}