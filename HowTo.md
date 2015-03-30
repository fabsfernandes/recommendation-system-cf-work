# Generating training and test files #

The first step to be done is to generate training and test files based on input ratings. The command is:

`java -cp recsys-cf.jar br.com.ufu.lsi.recsys.base.TestBaseGenerator -T/path/to/directory/in/which/the/files/will/be/created -R/path/to/ratings/file`

After that, check if the dirs 'test' and 'training' were correctly created.

# Running #

Now is just setup params and run it!

`java -jar recsys-cf.jar -R/path/to/ratings/file -P/path/to/output/predicted/rates/file -D/path/to/directory/in/wich/files/were/created`

**Optional parameters (default value)**

-T rmse threshold (0.8)

-F number of latent factors (10)

-O number of rounds for avoiding overfitting (5)

-B if ratings should be treated as boolean (false)



# Details #

**Input file format: `<user_id>\t<movie_id>\t<rating>`**

Example: https://recommendation-system-cf-work.googlecode.com/svn/trunk/recsys-cf/src/main/resources/ratings_example.txt