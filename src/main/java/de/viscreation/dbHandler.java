// * Ideas to be realize, but not jet  realized ):
 
// inserts
//long id = App.db.insert(tableName,data);
//long id = App.db.insert(someTypedObject);
//
//// updates
//data.id = id;
//long matchedRows = App.db.update(tableName,data,"WHERE id="+id); // replace by WHERE 
//boolean updatedSuccessed = App.db.replace(tableName,data); // replace by id
//
//// deletes
//long matchedRows = App.db.delete(tableName,whereSql); // delete by WHERE
//boolean updatedSuccessed = App.db.delete(tableName,data); // delete by id
//
//// get data
//
//// get only first value
//name = App.db.get("SELECT id FROM MesurmentTargets WHERE name LIKE 'test'",StorageResultType.VALUE); // adds LIMIT 1 to SQL  end
//name = App.db.getValue("SELECT id FROM MesurmentTargets WHERE name LIKE 'test'"); // adds LIMIT 1 to SQL  end
//
//
//// get only first row
//HashMap<String,String> row = App.db.get("SELECT * FROM MesurmentTargets WHERE name LIKE 'test'",StorageResultType.ROW); // adds LIMIT 1 to SQL  end
//HashMap<String,String> row = App.db.getRow("SELECT * FROM MesurmentTargets WHERE name LIKE 'test'"); // adds LIMIT 1 to SQL  end
//
//
//
//// get all rows
//ArrayCollection<HashMap<String,String>> rows = App.db.get("SELECT id FROM MesurmentTargets WHERE name LIKE 'test'",StorageResultType.ROWS); 
//ArrayCollection<HashMap<String,String>> rows = App.db.getRows("SELECT id FROM MesurmentTargets WHERE name LIKE 'test'");
//
//
//name = App.db.exec("SELECT id FROM MesurmentTargets WHERE name LIKE 'test'"); // returns ResultSet
//
//// get and typezed by TableName
//target = (MesuarmentTarget)  App.db.getRow("SELECT * FROM MesuarmentTargets WHERE name LIKE 'test'");
//targets = (Verctor<MesuarmentTarget>) App.db.getRows("SELECT * FROM MesuarmentTargets");
//
