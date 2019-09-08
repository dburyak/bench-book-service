package dburyak.benchmark.book

import groovy.util.logging.Slf4j
import io.micronaut.context.ApplicationContext

@Slf4j
class AppStarter {
    static void main(String[] args) {
        log.info 'start initialization'
        ApplicationContext.run()
        log.info 'initialization done'
    }
}
