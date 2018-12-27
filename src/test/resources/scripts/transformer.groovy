def transformUser(raw) {
    def model = [:]

    model['externalId'] = raw['id']
    model['username'] = raw['profile']['login']
    model['email'] = raw['profile']['emails'][0]
    model['display'] = raw['name']
    model['name'] = raw['name']

    model
}

def transformGroup(raw) {
    def model = [:]

    model['externalId'] = raw['id']
    model['email'] = raw['email']
    model['display'] = raw['name']

    model
}

def transform(entity) {
    def result
    switch (entity.kind) {
        case 'USER':
            result = transformUser(entity.raw)
            break
        case 'GROUP':
            result = transformGroup(entity.raw)
            break
        default:
            result = [:]
    }
    result
}

transform(entity)