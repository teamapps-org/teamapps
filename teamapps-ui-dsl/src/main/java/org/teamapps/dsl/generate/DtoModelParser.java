package org.teamapps.dsl.generate;

import org.teamapps.dsl.TeamAppsDtoParser;
import org.teamapps.dsl.generate.model.DtoModel;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DtoModelParser {

	public DtoModel parseModel(List<File> dtoFileDirectories) {
		List<TeamAppsDtoParser.ClassCollectionContext> classCollectionContexts = dtoFileDirectories.stream()
				.map(dir -> {
					try {
						return TeamAppsGeneratorUtil.parseClassCollections(dir);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				})
				.flatMap(Collection::stream)
				.collect(Collectors.toList());


		TeamAppsIntermediateDtoModel intermediate = new TeamAppsIntermediateDtoModel(classCollectionContexts);

		DtoModel model = new DtoModel(intermediate);



		 return null;
	}

}
