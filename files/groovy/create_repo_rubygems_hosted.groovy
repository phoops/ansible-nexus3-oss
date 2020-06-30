import groovy.json.JsonSlurper
import org.sonatype.nexus.repository.config.Configuration

parsed_args = new JsonSlurper().parseText(args)

configuration = repository.repositoryManager.newConfiguration()
configuration.repositoryName = parsed_args.name
configuration.recipeName = 'rubygems-hosted'
configuration.online = true
configuration.attributes = [
                storage: [
                        writePolicy: parsed_args.write_policy.toUpperCase(),
                        blobStoreName: parsed_args.blob_store,
                        strictContentTypeValidation: Boolean.valueOf(parsed_args.strict_content_validation)
                ]
        ]

def existingRepository = repository.getRepositoryManager().get(parsed_args.name)

if (existingRepository != null) {
    existingRepository.stop()
    configuration.attributes['storage']['blobStoreName'] = existingRepository.configuration.attributes['storage']['blobStoreName']
    existingRepository.update(configuration)
    existingRepository.start()
} else {
    repository.getRepositoryManager().create(configuration)
}
