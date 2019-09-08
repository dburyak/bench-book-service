import java.nio.charset.Charset

appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        pattern =
                '%date{yyyy-MM-dd HH:mm:ss.SSS} ' +
                        '%highlight(%-5level) ' + // Log level
                        ' [%20.20t] ' + // Thread
                        '%-40.40logger{39} : ' + // Logger
                        '%message%n' // Message
    }
}

root INFO, ['STDOUT']

logger 'io.vertx.config', WARN

logger 'dburyak.benchmark.book', TRACE
