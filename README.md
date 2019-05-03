# got-records


## Usage

### Step 1 - Import and Report
Run `lein import` for usage directions.

#### Examples: 
`lein import 1` - imports the included sample files and displays Output 1<br>
`lein import 1 user/local/some-data-file.txt` - imports the given data file and displays Output 1

### Step 2 - REST service

#### Dev
From the project directory, run

`lein repl`

At the repl prompt, start the system with

`(go)`

By default, you can access the service at `http://localhost:8000`.  In dev mode, the sample files in the /data
directory are loaded at startup, so you can run reports instantly.  To change this behavior, modify the `:seed-data`
setting under `:profiles -> :dev -> :env` (set it to an empty vector to not load anything).

#### Uberjar
There is a shell script in the project root named `run-server.sh`.  Executing this shell script will build an uberjar
(if one doesn't exist yet) and start the server at `http://localhost:8888`.  This version does not load any sample
files, so you will start with an empty "database".

### Tests
`lein test`


## Optional data generation
The `got-records.generate` namespace contains functions I used to generate record files.  If needed, new
sample data can be generated using the commented out forms at the end of the file.  The generation uses
clojure specs and customized test.check generators that, for example, generate reasonable names using
sample data from the US Census.  The source of this generated data is in the `resources` folder.
