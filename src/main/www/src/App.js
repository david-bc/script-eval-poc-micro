import React from "react";
import axios from "axios";

import { compose, withHandlers, withState } from "recompose";

import AceEditor from "react-ace";

import brace from "brace";

import "brace/mode/groovy";
import "brace/mode/json";
import "brace/theme/monokai";
import "brace/theme/github";

const EXAMPLES = [
  {
    label: "Const Example",
    src: `19 + 4`,
    params: "{}"
  },
  {
    label: "Add Example",
    src: `def add(a, b) {
  a + b
}

add(input.x, input.y)`,
    params: JSON.stringify({ x: 5, y: 6 }, null, 2)
  },
  {
    label: "Transformer (USER) Example",
    src: `def transformUser(raw) {
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

transform(input.entity)`,
    params: JSON.stringify(
      {
        entity: {
          kind: "USER",
          raw: {
            id: "asdf123",
            name: "David Espo",
            profile: {
              login: "davide-bc",
              emails: ["de@bc.com", "davide@bc.com"]
            }
          }
        }
      },
      null,
      2
    )
  }
];

const App = ({
  onSubmit,
  params,
  response,
  setExample,
  setParams,
  setSrc,
  src
}) => (
  <div className="App container">
    <div className="row">
      <div className="col">
        <h1>Script</h1>
        <div>
          <AceEditor
            mode="groovy"
            theme="monokai"
            width="100%"
            value={src}
            onChange={val => setSrc(val)}
            editorProps={{ $blockScrolling: true }}
          />
        </div>
        <select onChange={e => setExample(EXAMPLES[e.target.value])}>
          {EXAMPLES.map((ex, i) => (
            <option value={i} key={i}>
              {ex.label}
            </option>
          ))}
        </select>
      </div>
      <div className="col">
        <h1>Params</h1>
        <div>
          <AceEditor
            mode="json"
            theme="monokai"
            width="100%"
            value={params}
            onChange={val => setParams(val)}
            editorProps={{ $blockScrolling: true }}
          />
        </div>
        <button onClick={() => onSubmit()}>Run</button>
      </div>
    </div>
    <h1>Output</h1>
    <div>
      <AceEditor
        mode="json"
        theme="github"
        width="100%"
        value={response}
        readOnly={true}
        editorProps={{ $blockScrolling: true }}
      />
    </div>
  </div>
);

export default compose(
  withState("src", "setSrc", "19 + 6"),
  withState("params", "setParams", "{}"),
  withState("response", "setResponse", ""),
  withHandlers({
    onSubmit: ({ src, params, setResponse }) => () => {
      try {
        params = JSON.parse(params);
        console.log({ src, params });
        axios
          .post("http://localhost:8080/rpc/v1/sync", {
            jsonrpc: "2.0",
            method: "scripting.groovy.eval",
            params: {
              name: "Testing",
              src,
              params
            },
            id: Math.floor(Math.random() * 10000)
          })
          .then(res => {
            const response = JSON.stringify(res.data, null, 2);
            setResponse(response);
            console.log({ response });
          });
      } catch (e) {
        console.error(e);
      }
    },
    setExample: ({ setSrc, setParams }) => example => {
      console.log({ example });
      const { src, params } = example;
      setSrc(src, () => setParams(params));
    }
  })
)(App);
