package pro.paulek.data.api;

//TODO support for mariadb, mongodb
public enum DataModel {

    MYSQL,
    SQLITE;

    public static DataModel getModelByName(String string) {
        if(string.equalsIgnoreCase("mysql")){
            return DataModel.MYSQL;
        }
        return DataModel.SQLITE;
    }

}
