# got-records


## Usage

### Import
Run `lein import` for usage directions.

#### Examples: 
`lein import 1` - imports the included sample files and displays Output 1<br>
`lein import 1 user/local/some-data-file.txt` - imports the given data file and displays Output 1

## Optional data generation
The `got-records.generate` namespace contains functions I used to generate record files.  If needed, new
sample data can be generated using the commented out forms at the end of the file.  The generation uses
clojure specs and customized test.check generators that, for example, generate reasonable names using
sample data from the US Census.  The source of this generated data is in the `resources` folder.
