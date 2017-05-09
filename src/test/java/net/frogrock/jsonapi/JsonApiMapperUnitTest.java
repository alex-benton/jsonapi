package net.frogrock.jsonapi;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;

import net.frogrock.jsonapi.JsonApiMapper;
import net.frogrock.jsonapi.objects.BaseObject;
import net.frogrock.jsonapi.objects.SingleDocument;
import net.frogrock.jsonapi.sample.Article;
import net.frogrock.jsonapi.sample.Comment;
import net.frogrock.jsonapi.sample.Person;

public class JsonApiMapperUnitTest {

    @Test
    public void testStartup() throws Exception {
        JsonApiMapper mapper = new JsonApiMapper();

        ObjectMapper map = new ObjectMapper();
        map.setSerializationInclusion(Include.NON_EMPTY);

        Article a = createSampleArticle();

        Stopwatch sw = Stopwatch.createStarted();
        BaseObject obj = mapper.encode(Arrays.asList(a, a));
        sw.stop();
        System.out.println("encode time: " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");

        System.out.println(map.writeValueAsString(obj));

    }

    @Test
    public void testEncodeDecodeSingle() throws Exception {
        JsonApiMapper mapper = new JsonApiMapper();

        ObjectMapper map = new ObjectMapper();
        map.setSerializationInclusion(Include.NON_EMPTY);

        Article a = createSampleArticle();

        SingleDocument document = (SingleDocument) mapper.encode(a);

        Stopwatch sw = Stopwatch.createStarted();
        Article result = mapper.decode(document, Article.class);

        sw.stop();

        System.out.println("decode time: " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");

        assertEquals(a, result);
    }

    private Article createSampleArticle() {

        Article a = new Article();
        Person author = new Person();
        author.setFirstName("alex");
        author.setLastName("benton");
        author.setId("1");
        a.setId("1");
        a.setTitle("a fake article title");
        a.setAuthor(author);

        Comment one = new Comment();
        one.setId("10");
        one.setBody("first post");
        one.setAuthor(author);

        Person authorTwo = new Person();
        authorTwo.setId("2");

        Comment two = new Comment();
        two.setId("20");
        two.setBody("lol");
        two.setAuthor(authorTwo);

        a.setComments(Arrays.asList(one, two));

        return a;
    }
}
