//package pro.paulek.test;
//
//import org.junit.Test;
//import pro.paulek.data.sql.SqlBuilder;
//import pro.paulek.data.sql.SqlCondition;
//import pro.paulek.database.Database;
//import pro.paulek.database.MySQL;
//import pro.paulek.objects.guild.DiscordMessage;
//
//import java.sql.Connection;
//import java.time.Instant;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.Assert.fail;
//
//public class SqlBuilderTest {
//    private Database database;
//
//    public SqlBuilderTest() {
//
//        this.database = new MySQL(credentials);
//        this.database.init();
//    }
//
//    @Test
//    public void testSQLBuilder() {
//        try (Connection connection = database.getConnection()) {
//            var object = new SqlBuilder()
//                    .select()
//                    .table("message")
//                    .connection(connection)
//                    .type(DiscordMessage.class)
//                    .condition(SqlCondition.of("id", 6))
//                    .execute();
//
//            var object2 = new SqlBuilder()
//                    .insert()
//                    .table("message")
//                    .connection(connection)
//                    .record(new DiscordMessage(
//                            "superpaulek",
//                            "123456789",
//                            "123456789",
//                            "Hello, world!",
//                            DiscordMessage.MessageAction.NEW,
//                            Instant.now()
//                    ))
//                    .execute();
//
//            System.out.println(object2);
//            if (object.isPresent()) {
//                System.out.println(object.get().toString());
//            } else {
//                fail();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
